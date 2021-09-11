import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TestApp } from '../../app/models/test-app';
import { Carrier } from '../../carrier/models/carrier';
import { RestService } from '../../shared/services/rest.service';
import { Flight } from '../models/flight';
import { TestFlight } from '../models/test-flight';

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


  public startFlight(flight: TestFlight): Observable<any> {
    return this.rest.post(`flights/start`, {flight});
  }

  public start(carrier: Carrier, app: TestApp): Observable<any> {
    return this.rest.post(`flights/${carrier.id}/applications/${app.name}/start`);
  }


  public stopAll(): Observable<any> {
    return this.rest.post(`flights/stop`);
  }

  public stop(carrier: Carrier, app: TestApp): Observable<any> {
    return this.rest.post(`flights/${carrier.id}/applications/${app.name}/stop`);
  }
}
