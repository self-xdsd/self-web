import {Component, Input, OnInit} from '@angular/core';
import {Wallet} from "../../wallet";
import {ChartConfiguration} from "chart.js";
import {formatEur} from "../../../util/money";

@Component({
  selector: 'app-wallet-chart',
  templateUrl: './wallet-chart.component.html',
  styleUrl: './wallet-chart.component.css',
  standalone: false
})
export class WalletChartComponent implements OnInit {

  @Input() wallet!: Wallet;
  // Doughnut
  public doughnutChartLabels: string[] = [ 'Available', 'Debt' ];
  public doughnutChartDatasets?: ChartConfiguration<'doughnut'>['data']['datasets'];

  public doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: false
  };

  ngOnInit(): void {
    this.doughnutChartDatasets = [
      { data: [ this.wallet.available, this.wallet.debt] }
    ];
  }


  protected readonly formatEur = formatEur;
}
