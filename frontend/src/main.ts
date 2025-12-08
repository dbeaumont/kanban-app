import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideRouter, Routes } from '@angular/router';
import { AppComponent } from './app/app.component';
import { BoardListComponent } from './app/boards/board-list.component';
import { BoardDetailComponent } from './app/boards/board-detail.component';
import { LoginComponent } from './app/login.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'boards/:id', component: BoardDetailComponent },
  { path: '**', component: BoardListComponent }
];

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withFetch())
  ]
}).catch(err => console.error(err));
