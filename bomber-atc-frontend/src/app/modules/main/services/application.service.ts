import {Injectable} from "@angular/core";
import {RestService} from "../../libs/services/rest.service";
import {empty, Observable, of} from "rxjs";
import {Carrier} from "../model/carrier";
import {Application} from "../model/application";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {

  constructor(private readonly rest: RestService) {
  }

  public getActiveCarriers(): Observable<Carrier[]> {
    return this.rest.get('carriers/active')
        .pipe(
            catchError((err, caught) => of([]))
        );
  }

  startAll(): Observable<any> {
    return this.rest.post(`carriers/applications/start`);
  }

  public startApp(instance: Carrier, app: Application): Observable<any> {
    return this.rest.post(`carriers/${instance.id}/applications/${app.name}/start`);
  }


  stopAllApps(): Observable<any> {
    return this.rest.post(`carriers/applications/stop`);
  }

  public stopApp(instance: Carrier, app: Application): Observable<any> {
    return this.rest.post(`carriers/${instance.id}/applications/${app.name}/stop`);
  }
}
