import {Component} from '@angular/core';
import {AuthService} from "../../../shared/services/auth.service";
import {Router} from "@angular/router";
import {from} from "rxjs";
import {flatMap} from "rxjs/operators";

@Component({
  selector: 'atc-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.css']
})
export class MainMenuComponent {

  constructor(private readonly service: AuthService, private readonly router: Router) {
  }

  logout() {
    this.service.logout()
        .pipe(
            flatMap(() => from(this.router.navigate(['/login'])))
        )
        .subscribe();
  }
}
