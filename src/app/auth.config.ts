export const authConfig = {
  // Domain should be the Auth0 tenant host without protocol or trailing slash
  domain: 'dev-m362yc372ygvz1ny.us.auth0.com',
  clientId: 'PWhKXAtNqwZuxF1njrQ55ulHNS1aYIr5',
  authorizationParams: {
    audience: 'https://rlibrary.com',
    redirect_uri: window.location.origin + '/'
  },
  httpInterceptor: {
    allowedList: ['http://localhost:8080/api/*']
  }
};
