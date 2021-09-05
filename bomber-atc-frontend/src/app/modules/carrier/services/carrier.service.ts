import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestService } from '../../shared/services/rest.service';
import { Carrier } from '../models/carrier';

@Injectable({
  providedIn: 'root'
})
export class CarrierService {

  constructor(private readonly rest: RestService) {
  }

  public getActive(): Observable<Carrier[]> {
    return this.rest.get('carriers/active')
        .pipe(
            catchError((err, caught) => of([]))
        );
  }
}
