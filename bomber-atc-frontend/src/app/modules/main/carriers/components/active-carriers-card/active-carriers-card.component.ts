import {Component, OnInit} from '@angular/core';
import {Carrier} from "../../../model/carrier";
import {ApplicationService} from "../../../services/application.service";
import {Observable, of, Subscription, timer} from "rxjs";
import {flatMap, tap} from "rxjs/operators";

@Component({
  selector: 'carriers-active-card',
  templateUrl: './active-carriers-card.component.html'
})
export class ActiveCarriersCardComponent implements OnInit {
  carriers: Observable<Carrier[]>;

  constructor(private readonly service: ApplicationService) {
  }

  ngOnInit() {
    this.carriers = timer(0, 3000)
        .pipe(
            flatMap(() => this.service.getActiveCarriers())
        );
  }
}
