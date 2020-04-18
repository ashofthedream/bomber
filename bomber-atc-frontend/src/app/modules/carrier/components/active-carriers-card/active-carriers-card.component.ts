import {Component, OnInit} from '@angular/core';
import {Carrier} from "../../models/carrier";
import {CarrierService} from "../../services/carrier.service";
import {Observable, timer} from "rxjs";
import {flatMap} from "rxjs/operators";

@Component({
  selector: 'carriers-active-card',
  templateUrl: './active-carriers-card.component.html'
})
export class ActiveCarriersCardComponent implements OnInit {
  carriers: Observable<Carrier[]>;

  constructor(private readonly service: CarrierService) {
  }

  ngOnInit() {
    this.carriers = timer(0, 3000)
        .pipe(
            flatMap(() => this.service.getActiveCarriers())
        );
  }
}
