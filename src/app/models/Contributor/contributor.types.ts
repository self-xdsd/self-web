
export interface Contributor {
  contracts: Contract[];
  provider: string;
  username: string;
}

export interface Invoice {
  id: number,
  createdAt: Date,
  isPaid: boolean,
  amount: string,
  totalAmount: string,
  latestPayment?: InvoicePayment
}

export type InvoicePayment = {
  "status": string,
  failReason: string,
  "transactionId": string,
  "timestamp": Date
}

export interface ITask {
  assignmentDate: Date;
  deadline: Date;
  estimation: number;
  issueId: string;
  value: number;
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
