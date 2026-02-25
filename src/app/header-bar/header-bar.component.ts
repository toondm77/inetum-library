import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-header-bar',
  standalone: true,
  templateUrl: './header-bar.component.html',
  styleUrl: './header-bar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderBarComponent {}
