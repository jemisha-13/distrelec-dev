import { Request } from 'express';
import fetch from 'node-fetch';

const hostnameToSiteId = new Map<string, string>();

export async function getSiteId(req: Request, apiUrl: string): Promise<string> {
  const mappedSiteId = hostnameToSiteId.get(req.hostname);
  if (mappedSiteId) {
    return mappedSiteId;
  } else {
    const siteId = await getSiteIdFromService(req, apiUrl);
    if (siteId) {
      hostnameToSiteId.set(req.hostname, siteId);
    }
    return siteId;
  }
}

async function getSiteIdFromService(req: Request, apiUrl: string): Promise<string> {
  const requestURL = `${apiUrl}/rest/v2/basesites/match?host=${req.hostname}`;
  const response = await fetch(requestURL);

  if (response.ok) {
    const responseData = (await response.text()) as string;
    if (responseData.startsWith('"')) {
      return JSON.parse(responseData);
    } else {
      return responseData;
    }
  } else {
    return undefined;
  }
}
