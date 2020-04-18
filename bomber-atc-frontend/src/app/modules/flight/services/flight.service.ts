import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Carrier} from "../../carrier/models/carrier";
import {Application} from "../../main/models/application";
import {RestService} from "../../shared/services/rest.service";
import {Flight} from "../models/flight";

@Injectable({
  providedIn: 'root'
})
export class FlightService {

  constructor(private readonly rest: RestService) {
  }

  public geAll(): Observable<Flight[]> {
    return this.rest.post(`flights`);
  }

  public getActive(): Observable<Flight> {
    return this.rest.post(`flights/active`);
  }


  public startAll(): Observable<any> {
    return this.rest.post(`flights/start`);
  }

  public start(carrier: Carrier, app: Application): Observable<any> {
    return this.rest.post(`flights/${carrier.id}/applications/${app.name}/start`);
  }


  public stopAll(): Observable<any> {
    return this.rest.post(`flights/stop`);
  }

  public stop(carrier: Carrier, app: Application): Observable<any> {
    return this.rest.post(`flights/${carrier.id}/applications/${app.name}/stop`);
  }
}
