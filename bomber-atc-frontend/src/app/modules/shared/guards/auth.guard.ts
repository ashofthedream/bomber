import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";
import {AuthService} from "../services/auth.service";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(private readonly service: AuthService, private readonly router: Router) {
  }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.checkAuth();
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.checkAuth();;
  }


  private checkAuth() {
    return this.service.authenticated()
        .pipe(
            tap(authenticated => this.redirectIfNotAuthenticated(authenticated))
        );
  }

  private redirectIfNotAuthenticated(authenticated: boolean) {
    if (authenticated)
      return;

    this.router.navigate(['/login']);
  }
}
