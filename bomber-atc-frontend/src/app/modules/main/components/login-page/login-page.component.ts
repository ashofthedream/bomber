import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../../../auth/services/auth.service";
import { Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { AtcState } from "../../../shared/store/state/atc.state";
import { Login } from "../../../auth/store/actions/auth.actions";

@Component({
  selector: 'atc-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {
  form: FormGroup;

  constructor(private readonly fb: FormBuilder,
              private readonly authService: AuthService,
              private readonly router: Router,
              private readonly store: Store<AtcState>) {
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

    if (this.form.valid)
      this.store.dispatch(new Login(this.form.value))
  }
}
