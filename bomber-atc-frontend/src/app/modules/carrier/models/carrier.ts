import { Application } from '../../main/models/application';

export class Carrier {
  id: string;
  uri: string;
  app: Application;

  selected = true;
}
