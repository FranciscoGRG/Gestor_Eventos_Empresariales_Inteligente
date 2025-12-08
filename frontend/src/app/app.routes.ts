import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page-component/landing-page-component';
import { LoginComponent } from './components/login-component/login-component';
import { RegisterComponent } from './components/register-component/register-component';
import { EventComponent } from './components/event-component/event-component';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
    {path: '', component: LandingPageComponent},
    {path: 'login', component: LoginComponent},
    {path: 'events', component: EventComponent, canActivate: [authGuard]},
    {path: 'register', component: RegisterComponent}
];
