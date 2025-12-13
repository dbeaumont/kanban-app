import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { environment } from '../environments/environment';

// Ensures cookies flow to the BFF and redirects to login on 401.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Send cookies for the BFF; avoid forcing X-Requested-With so Spring can issue redirects.
  const withCreds = req.clone({ withCredentials: true });
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
