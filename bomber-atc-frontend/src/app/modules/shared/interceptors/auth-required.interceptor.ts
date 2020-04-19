import {Injectable} from "@angular/core";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {Router} from "@angular/router";
import {AuthService} from "../services/auth.service";

@Injectable()
export class AuthRequiredInterceptor implements HttpInterceptor {

  constructor(private readonly service: AuthService, private readonly router: Router) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
        .pipe(
            catchError(err => {
              if (err instanceof HttpErrorResponse && err.status === 401) {
                this.service.clearAuth();
                this.router.navigate(['/login'])
              }

              return throwError(err);
            })
        );
  }
}
