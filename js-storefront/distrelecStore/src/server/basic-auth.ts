import { Request, Response } from 'express';
import * as basicAuth from 'express-basic-auth';

const md5 = require('md5');

let internalIpsRegEx: RegExp;
let skipBasicAuthSitesSet: Set<string>;
let users: Map<string, string>;

export function distBasicAuth(environment) {
  internalIpsRegEx = new RegExp(environment.internalIps);
  skipBasicAuthSitesSet = new Set<string>(environment.skipBasicAuthSites);
  users = new Map<string, string>(environment.basicAuthUsers);

  return (req: Request, res: Response, next) => {
    if (shouldRequestBasicAuth(req)) {
      basicAuth({
        authorizer: distAuthorizer,
        challenge: true,
      })(req, res, next);
    } else {
      next();
    }
  };
}

// checks if ip is internal ip and if basic auth should by bypassed
function shouldRequestBasicAuth(req: Request): boolean {
  if (isProduction(req) || skipBasicAuthSitesSet.has(req.hostname)) {
    return false;
  }
  if (isBypassed(req.ip)) {
    return false;
  }
  const forwardedFor = req.get('X-Forwarded-For');
  if (forwardedFor) {
    const ips = forwardedFor.split(',');
    const matches = ips.find((ip) => isBypassed(ip));
    if (matches) {
      return false;
    }
  }
  return true;
}

function isProduction(req: Request): boolean {
  const elements: string[] = req.hostname.split('.');

  return elements[0] === 'www';
}

function isBypassed(ip: string): boolean {
  return internalIpsRegEx.test(ip);
}

function distAuthorizer(username: string, password: string): boolean {
  const expectedPassword = users.get(username);
  if (expectedPassword) {
    const md5Password = md5(password);
    return basicAuth.safeCompare(md5Password, expectedPassword);
  } else {
    return false;
  }
}
