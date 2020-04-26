import { Injectable } from "@angular/core";
import { LocalStorageService } from "./local-storage.service";
import { Observable } from "rxjs";
import { User } from "../models/user";
import { switchIfEmpty } from "../rx/switch-if-empty";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private readonly storage: LocalStorageService) {
  }

  getUser(): Observable<User> {
    return this.storage.load<User>('bomber.atc.user')
        .pipe(switchIfEmpty(null));
  }

  saveUser(user: User) {
    this.storage.save('bomber.atc.user', user)
  }

  removeUser() {
    this.storage.remove('bomber.atc.user');
  }
}
