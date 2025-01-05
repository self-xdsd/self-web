import {Wallet} from "./wallet";
import {ProjectManager} from "../projectManager";

export interface Project {
  repoFullName: string;
  provider: string;
  selfOwner: string;
  manager: ProjectManager;
  wallet: Wallet;
}
