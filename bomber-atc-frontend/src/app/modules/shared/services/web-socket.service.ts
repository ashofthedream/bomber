import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { webSocket } from 'rxjs/webSocket';
import { Carrier } from '../../carrier/models/carrier';

export interface ActiveCarriersEvent {
  type: string;
  carriers: Carrier[];
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private socket = webSocket('ws://localhost:4200/atc/socket');

  getCarriers(): Observable<Carrier[]> {
    return this.socket
        .pipe(
          filter((event: ActiveCarriersEvent) => event.type === 'ACTIVE_CARRIERS'),
          map((event: ActiveCarriersEvent) => event.carriers)
        );
  }
}
