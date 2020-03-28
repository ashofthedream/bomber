import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {NzNotificationService} from 'ng-zorro-antd';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class RestService {
  protected constructor(private readonly http: HttpClient, private readonly  notification: NzNotificationService) {
  }

  public build(): HttpRequestBuilder {
    return new HttpRequestBuilder(this.http, this.notification)
        .baseUrl('')
        .withCredentials();
  }

  public get(url: string): Observable<any> {
    return this.build()
        .get(url);
  }

  public post(url: string, body: any = null): Observable<any> {
    return this.build()
        .body(body)
        .post(url);
  }
}


export declare type HttpHeader = { [header: string]: string | string[] };
export declare type HttpParam = { [param: string]: string | string[] };

export declare type HttpOptions = {
  headers?: HttpHeaders | HttpHeader;
  observe?: 'body';
  params?: HttpParams;
  reportProgress?: boolean;
  responseType?: 'json';
  withCredentials?: boolean;
};


export declare type Notification = {
  title: string,
  content: string;
};


export class HttpRequestBuilder {

  _baseUrl: string;
  _body: any;

  options: HttpOptions = {};

  notification: Notification;

  constructor(private http: HttpClient, private notificationService: NzNotificationService) {
  }

  baseUrl(baseUrl: string): HttpRequestBuilder {
    this._baseUrl = baseUrl;
    return this;
  }

  body(body: any = null): HttpRequestBuilder {
    this._body = body;
    return this;
  }

  notify(title: string, content: string = ''): HttpRequestBuilder {
    if (title)
      this.notification = {title, content};

    return this;
  }

  withCredentials(): HttpRequestBuilder {
    this.options.withCredentials = true;
    return this;
  }

  header(header: string, value: any): HttpRequestBuilder {
    return this;
  }


  param(name: string, value: any): HttpRequestBuilder {
    if (!this.options.params)
      this.options.params = new HttpParams();

    this.options.params.append(name, value);

    return this;
  }

  get(url: string): Observable<any> {
    return this.http.get(`${this._baseUrl}/${url}`, this.options)
        .pipe(
            tap(this.showNotificationIfNeeded)
        )
  }

  post(url: string): Observable<any> {
    return this.http.post(`${this._baseUrl}/${url}`, this._body, this.options)
        .pipe(
            tap(this.showNotificationIfNeeded)
        )
  }

  private showNotificationIfNeeded = () => {
    const notification = this.notification;
    if (notification)
      this.notificationService.success(notification.title, notification.content)
  };
}
