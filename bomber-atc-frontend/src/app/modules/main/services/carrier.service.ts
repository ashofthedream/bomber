import {Injectable} from "@angular/core";
import {RestService} from "../../libs/services/rest.service";
import {empty, Observable, of} from "rxjs";
import {Carrier} from "../model/carrier";
import {Application} from "../model/application";
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
