export interface Wallet {
  type: string;
  active: boolean;
  cash: number;
  debt: number;
  available: number;
}
