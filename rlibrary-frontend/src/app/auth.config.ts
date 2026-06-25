import { AuthConfig } from '@auth0/auth0-angular';
import { environment } from '../environments/environment';

export const authConfig: AuthConfig = {
  domain: 'dev-m362yc372ygvz1ny.us.auth0.com',
  clientId: 'PWhKXAtNqwZuxF1njrQ55ulHNS1aYIr5',
  authorizationParams: {
    audience: 'https://rlibrary.com',
    redirect_uri: window.location.origin + '/'
  },
  cacheLocation: 'localstorage',
  httpInterceptor: {
    allowedList: [`${environment.apiBase}/*`]
  }
};
