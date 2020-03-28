import { Injectable } from "@angular/core";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { tap } from "rxjs/internal/operators/tap";


@Injectable()
export class RequestLogHttpInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(request)
        .pipe(
            tap(event => {
                  if (event instanceof HttpResponse) {
                    const response = event as HttpResponse<any>;
                    console.log(`${request.method} ${request.url}  >>  ${response.status}`, request.body, response.body);
                  }
                },
                e => console.log(`${request.method} ${request.url}  >> `, e)
            ));
  }
}
