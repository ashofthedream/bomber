import {User} from "../models/user";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  public saveUser(user: User) {
    this.save('bomber.user', user);
  }

  public loadUser(): User {
    return this.load('bomber.user', new User());
  }

  public clearUser() {
    this.remove('bomber.user')
  }

  private save(key: string, value: any) {
    console.log(`storage save with key: ${key}`, value)
    localStorage.setItem(key, JSON.stringify(value));
  }

  private load<T>(key: string, assign: T = null): T {
    const item = localStorage.getItem('bomber.user');
    if (!item)
      return null;

    const value = JSON.parse(item);

    if (!assign) {
      console.log(`storage load by key: ${key}`, value)
      return value;
    }

    return Object.assign(assign, value);
  }

  private remove(key: string) {
    console.log(`storage remove key: ${key}`);
    localStorage.removeItem(key);
  }
}
