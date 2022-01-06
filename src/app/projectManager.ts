export interface ProjectManager {
  id: number;
  userId: string;
  username: string;
  provider: string;
  commission: number;
  contributorCommission: number;
  token: string;
}
