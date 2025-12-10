import 'zone.js'; // required for Angular change detection
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideRouter, Routes } from '@angular/router';
import { AppComponent } from './app/app.component';
import { BoardListComponent } from './app/boards/board-list.component';
import { BoardDetailComponent } from './app/boards/board-detail.component';
import { LoginComponent } from './app/login.component';
import { authInterceptor } from './app/auth.interceptor';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'boards/:id', component: BoardDetailComponent },
  { path: '**', component: BoardListComponent }
];

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor])
    )
  ]
}).catch(err => console.error(err));
