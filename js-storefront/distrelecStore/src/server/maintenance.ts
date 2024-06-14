import { Request, Response } from 'express';
import fetch from 'node-fetch';
import { MaintenanceResponse } from '@model/maintenance.model';

const siteIdResolver = require('./site-id-resolver.ts');

// refresh internal in milliseconds
const refreshMaintenanceActiveInterval = 60_000;

const internalIpsSet: Set<string> = new Set<string>();
const maintenanceActive: Map<string, boolean> = new Map<string, boolean>();
const maintenanceHtmlMap: Map<string, string> = new Map<string, string>();
const maintenanceHtmlTimestamp: Map<string, Date> = new Map<string, Date>();

// timestamp when maintenance
let maintenanceActiveTimestamp: Date;

/*
 * Returns an url onto a maintenance 503 url.
 */
function getMaintenanceHtmlUrl(req: Request): string {
  const elements: string[] = req.hostname.split('.');

  if (elements[1] === 'local') {
    return 'https://pretest.distrelec.at/error/503.html';
  } else {
    return req.protocol + '://' + req.hostname + '/error/503.html';
  }
}

export async function showMaintenancePage(req: Request, res: Response, apiUrl: string): Promise<void> {
  if (!maintenanceHtmlMap.has(req.hostname)) {
    await populateMaintenanceHtml(req, apiUrl);
  }

  if (maintenanceTimestampExpires(req)) {
    // refresh maintenance html in background
    populateMaintenanceHtml(req, apiUrl);
  }

  const maintenanceHtml = maintenanceHtmlMap.get(req.hostname);

  res.status(503).send(maintenanceHtml);
}

async function populateMaintenanceHtml(req: Request, apiUrl: string): Promise<void> {
  const maintenanceHtmlUrl = getMaintenanceHtmlUrl(req);
  const response = await fetch(maintenanceHtmlUrl);

  if (response.ok) {
    const body = await response.text();
    maintenanceHtmlMap.set(req.hostname, body);
    maintenanceHtmlTimestamp.set(req.hostname, new Date());
  } else {
    console.error('Could not load maintenance page from ' + maintenanceHtmlUrl);
  }
}

/*
 * Checks if an IP is whitelisted - internal IP.
 */
function isIpWhitelisted(req: Request, displayMaintenanceForLocal: boolean): boolean {
  if (req.ips.length === 0) {
    return !displayMaintenanceForLocal; // internal ip
  } else {
    const ips = req.header('x-forwarded-for').split(',') || req.connection.remoteAddress;

    for (const ip of ips) {
      if (internalIpsSet.has(ip.trim())) {
        return true;
      }
    }

    return false;
  }
}

/*
 * Checks if maintenance timestamp is expired
 */
function maintenanceTimestampExpires(req: Request): boolean {
  const timestamp = maintenanceHtmlTimestamp.get(req.hostname);
  if (timestamp) {
    const duration: number = new Date().valueOf() - timestamp.valueOf();
    return duration > refreshMaintenanceActiveInterval;
  } else {
    return true;
  }
}

/*
 * Gets maintenance flags from a backend and updates internal sets.
 */
async function populateMaintenanceActive(apiUrl: string) {
  // refresh timestamp
  maintenanceActiveTimestamp = new Date();

  const requestUrl = apiUrl + '/rest/v2/basesites/mutable';
  const response = await fetch(requestUrl);

  if (response.ok) {
    const parsedBody = (await response.json()) as MaintenanceResponse;
    for (const baseSite of parsedBody.baseSites) {
      maintenanceActive.set(baseSite.uid, baseSite.maintenanceActive);
    }
  } else {
    // activate maintenance on all sites
    for (const baseSiteUid of maintenanceActive.keys()) {
      maintenanceActive.set(baseSiteUid, true);
    }
  }
}

export async function shouldDisplayMaintenance(
  req: Request,
  apiUrl: string,
  displayMaintenanceForLocal: boolean,
): Promise<boolean> {
  if (internalIpsSet.size === 0) {
    await populateInternalIpsSet(apiUrl);
  }

  const ipWhitelisted: boolean = isIpWhitelisted(req, displayMaintenanceForLocal);
  return !ipWhitelisted;
}

/*
 * Parses a comma separate internal ips string to a set.
 */
async function populateInternalIpsSet(apiUrl: string) {
  const requestUrl = apiUrl + '/rest/v2/basesites';
  const response = await fetch(requestUrl);

  if (response.ok) {
    const parsedBody = (await response.json()) as { internalIps: string[] };

    parsedBody.internalIps.forEach((internalIp) => {
      internalIpsSet.add(internalIp);
    });
  }
}

export async function isMaintenanceActiveOnSite(req: Request, apiUrl: string): Promise<boolean> {
  if (maintenanceActive.size === 0) {
    await populateMaintenanceActive(apiUrl);
  }

  const siteId = await siteIdResolver.getSiteId(req, apiUrl);

  if (siteId) {
    const isMaintenanceActive = maintenanceActive.get(siteId);

    if (maintenanceTimestampExpires(req)) {
      // refresh maintenance flags in background
      populateMaintenanceActive(apiUrl);
    }

    if (isMaintenanceActive !== undefined) {
      return isMaintenanceActive;
    }
  }

  // site id is not resolved and therefore maintenance is active
  return true;
}
