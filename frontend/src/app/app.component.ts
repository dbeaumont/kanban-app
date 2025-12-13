import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <header class="topbar">
      <h1>Kanban2</h1>
      <button class="ghost" (click)="login()">Login</button>
    </header>
    <main class="content">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    .topbar { display:flex; align-items:center; justify-content:space-between; padding:12px 20px; background:#0f172a; color:#fff; }
    .content { padding:16px; }
    .ghost { background:transparent; color:#fff; border:1px solid #fff; padding:6px 12px; border-radius:6px; cursor:pointer; }
  `]
})
export class AppComponent {
  login() {
    // Trigger OIDC login via the BFF endpoint proxied by nginx
    const authBase = environment.apiBaseUrl.replace(/\/api$/, '');
    window.location.href = `${authBase}/oauth2/authorization/keycloak`;
  }
}
