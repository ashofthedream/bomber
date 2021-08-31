import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { empty, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from '../../shared/models/user';
import { RestService } from '../../shared/services/rest.service';
import { UserService } from '../../shared/services/user.service';
import { LoginRequest } from '../model/login.request';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private readonly rest: RestService,
              private readonly userService: UserService,
              private readonly router: Router) {
  }


  login(login: LoginRequest): Observable<User> {
    return this.rest
        .build()
        .body(login)
        .post('login')
        .pipe(
            catchError((err, caught) => {
              console.log('not working bro', err);
              return empty();
            })
        );
  }

  loginSuccessful(user: User) {
    this.userService.saveUser(user);
    this.router.navigateByUrl('/');
  }


  logout(): Observable<any> {
    return this.rest
        .build()
        .post('logout');
  }

  logoutSuccessful() {
    this.userService.removeUser();
    this.router.navigateByUrl('/login');
  }
}
