import {Component} from '@angular/core';
import {AuthService} from "./modules/shared/services/auth.service";


@Component({
  selector: 'atc-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private readonly authService: AuthService) {
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}

