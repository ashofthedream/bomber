import {Injectable} from "@angular/core";
import {RestService} from "../../libs/services/rest.service";
import {Observable} from "rxjs";
import {Instance} from "../model/instance";

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {

  constructor(private readonly rest: RestService) {
  }

  public getActiveApplications(): Observable<Instance[]> {
    return this.rest.get('dispatchers/active');
  }

  public startApp(instance: Instance): Observable<any> {
    return this.rest.post(`dispatchers/start/${instance.id}`);
  }

  public shutdownApp(instance: Instance): Observable<any> {
    return this.rest.post(`dispatchers/shutdown/${instance.id}`);
  }
}
