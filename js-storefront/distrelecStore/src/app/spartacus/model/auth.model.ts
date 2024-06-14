declare module 'angular-oauth2-oidc/types' {
  export interface TokenResponse {
    language?: string;
  }
}

export interface LoginErrorMessage {
  country: string;
  href: string;
}

export type PasswordUpdateResult = 'SUCCESS' | 'TOKEN_INVALIDATED' | 'ERROR' | 'TOKEN_EMPTY';

export interface PasswordUpdateResponse {
  value: PasswordUpdateResult;
}

export interface SetInitialPasswordRequest {
  checkPassword: string;
  password: string;
  token: string;
  migration: boolean;
}
