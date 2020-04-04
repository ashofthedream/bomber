import {Application} from "./application";

export class Carrier {
  id: string;
  uri: string;
  app: Application;

  selected: boolean = true;
}
