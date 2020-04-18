import {Injectable} from "@angular/core";
import {RestService} from "./rest.service";
import {Observable, of} from "rxjs";
import {User} from "../models/user";
import {Login} from "../models/login";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user: User;

  constructor(private readonly rest: RestService) {
    this.user = this.loadUser();
  }

  authenticated(): Observable<boolean> {
    return of(this.isAuthenticated());
  }

  isAuthenticated(): boolean {
    return !!this.user;
  }

  cleanAuth() {
    this.user = null;
  }

  login(login: Login): Observable<User> {
    return this.rest
        .build()
        .body(login)
        .post('/login')
        .pipe(
            tap((user: User) => {
              this.user = user;
              this.saveUser(user);
            })
        )
  }

  logout(): Observable<boolean> {
    return this.rest
        .build()
        .post('/logout');
  }

  private saveUser(user: User) {
    console.log('saveUser: ', user)
    localStorage.setItem('bomber.user', JSON.stringify(user));
  }

  private loadUser(): User {
    const user = JSON.parse(localStorage.getItem('bomber.user'));
    console.log('loadUser: ', user)
    return user;
  }
}
