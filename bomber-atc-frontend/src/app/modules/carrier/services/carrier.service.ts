import {Injectable} from "@angular/core";
import {RestService} from "../../shared/services/rest.service";
import {Observable, of} from "rxjs";
import {Carrier} from "../models/carrier";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class CarrierService {

  constructor(private readonly rest: RestService) {
  }

  public getActiveCarriers(): Observable<Carrier[]> {
    return this.rest.get('carriers/active')
        .pipe(
            catchError((err, caught) => of([]))
        );
  }
}
