import { Component } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-login',
  template: `
    <section class="panel">
      <h2>Login via Keycloak</h2>
      <p>Redirection vers le BFF pour l'authentification.</p>
      <button (click)="login()">Se connecter</button>
    </section>
  `,
  styles: [`.panel { max-width:420px; margin:40px auto; padding:20px; border:1px solid #ddd; border-radius:12px; }`]
})
export class LoginComponent {
  login() { window.location.href = '/oauth2/authorization/keycloak'; }
}
