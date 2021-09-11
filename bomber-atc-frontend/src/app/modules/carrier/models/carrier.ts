import { TestApp } from '../../app/models/test-app';

export interface Carrier {
  id: string;
  uri: string;
  apps: TestApp[];
}
