import {Injectable} from "@angular/core";
import {RestService} from "./rest.service";
import {Observable, of} from "rxjs";
import {User} from "../models/user";
import {Login} from "../models/login";
import {tap} from "rxjs/operators";
import {LocalStorageService} from "./local-storage.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user: User;

  constructor(private readonly rest: RestService, private readonly storage: LocalStorageService) {
    this.user = this.storage.loadUser();

  }

  authenticated(): Observable<boolean> {
    return of(this.isAuthenticated());
  }

  isAuthenticated(): boolean {
    return !!this.user;
  }

  clearAuth() {
    this.user = null;
    this.storage.clearUser();
  }

  getUser(): Observable<User> {
    return this.rest
        .build()
        .get('users/current')
        .pipe(
            tap((user: User) => this.updateUser(user))
        )
  }

  login(login: Login): Observable<User> {
    return this.rest
        .build()
        .body(login)
        .post('login')
        .pipe(
            tap((user: User) => this.updateUser(user))
        )
  }

  private updateUser(user: User) {
    this.user = user;
    this.storage.saveUser(user);
  }

  logout(): Observable<boolean> {
    this.clearAuth();
    return this.rest
        .build()
        .post('logout');
  }
}
