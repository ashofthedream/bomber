import { NzTreeNodeOptions } from 'ng-zorro-antd/tree';
import { Carrier } from '../../carrier/models/carrier';

export interface AppState {
  plan: any[];
}

export interface AppTreeNode extends NzTreeNodeOptions {
  children: AppTreeNode[];
  carriers: string[];
}

export const initialAppState: AppState = {
  plan: []
};
