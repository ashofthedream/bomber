import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../../shared/services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'atc-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {
  form: FormGroup;

  constructor(private readonly fb: FormBuilder,
              private readonly authService: AuthService,
              private readonly router: Router) {
  }

  ngOnInit() {
    this.form = this.fb.group({
      username: [null, [Validators.required, Validators.minLength(1)]],
      password: [null, [Validators.required, Validators.minLength(1)]]
    });
  }


  login() {
    for (const name in this.form.controls) {
      const control = this.form.controls[name];
      control.markAsDirty();
      control.updateValueAndValidity();
    }

    this.authService.login(this.form.value)
        .subscribe(user => {
          if (user)
            this.router.navigate(['/dashboard']);
        });
  }
}
