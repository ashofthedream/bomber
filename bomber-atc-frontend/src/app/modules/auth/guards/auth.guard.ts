import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree
} from "@angular/router";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AtcState } from "../../shared/store/state/atc.state";
import { Store } from "@ngrx/store";
import { map } from "rxjs/operators";
import { isAuthenticated } from "../store/selectors/auth.selectors";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {

  readonly login = this.router.parseUrl('/login');

  constructor(private readonly store: Store<AtcState>, private readonly router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.checkAuth();
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.checkAuth();
  }

  private checkAuth(): Observable<boolean | UrlTree> {
    return this.store.select(isAuthenticated)
        .pipe(
            map(auth => auth ? true : this.login),
        );
  }
}
