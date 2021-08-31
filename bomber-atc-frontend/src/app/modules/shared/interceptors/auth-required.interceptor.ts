import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LogoutSuccess } from '../../auth/store/auth.actions';
import { AtcState } from '../store/atc.state';

@Injectable()
export class AuthRequiredInterceptor implements HttpInterceptor {

  constructor(private readonly store: Store<AtcState>) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
        .pipe(
            catchError(err => {
              if (err instanceof HttpErrorResponse && err.status === 401) {
                this.store.dispatch(new LogoutSuccess());
              }

              return throwError(err);
            })
        );
  }
}
