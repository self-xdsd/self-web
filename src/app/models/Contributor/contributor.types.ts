
export interface Contributor {
  contracts: Contract[];
  provider: string;
  username: string;
}

export interface Invoice {

}

export interface ITask {

}


export interface Contract {
  hourlyRate: string;
  markedForRemoval: string;
  projectWalletType: string;
  revenue: string;
  value: string;
  id: ContractId;


}

export interface ContractId {
  repoFullName: string;
  contributorUsername: string;
  provider: string;
  role: string;
}
