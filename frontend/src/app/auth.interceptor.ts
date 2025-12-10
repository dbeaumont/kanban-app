import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { environment } from '../environments/environment';

// Ensures cookies flow to the BFF and redirects to login on 401.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const withCreds = req.clone({
    withCredentials: true,
    setHeaders: { 'X-Requested-With': 'XMLHttpRequest' }
  });
  return next(withCreds).pipe(
    catchError(err => {
      if (err instanceof HttpErrorResponse && err.status === 401) {
        const authBase = environment.apiBaseUrl.replace(/\/api$/, '');
        window.location.href = `${authBase}/oauth2/authorization/keycloak`;
      }
      return throwError(() => err);
    })
  );
};
