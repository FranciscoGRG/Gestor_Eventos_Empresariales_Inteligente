import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthRequestModel } from '../models/auth-request-model';
import { AuthResponseModel } from '../models/auth-response-model';
import { Observable, tap } from 'rxjs';
import { CurrentUserDataModel } from '../models/current-user-data-model';
import { UserModel } from '../models/user-model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8082/api/auth';
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly USER_DATA_KEY = 'currentUserData';
  private http = inject(HttpClient);
  private router = inject(Router);

  private loggedInUser: WritableSignal<CurrentUserDataModel | null> = signal(this.getInitialUser());

  constructor() { }

  private getInitialUser(): CurrentUserDataModel | null {
    const userJson = localStorage.getItem(this.USER_DATA_KEY);
    if (userJson) {
      try {
        return JSON.parse(userJson) as CurrentUserDataModel;
      } catch (error) {
        this.logout();
        return null;
      }
    }
    return null;
  }

  currentUser() {
    return this.loggedInUser();
  }

  login(credentials: AuthRequestModel): Observable<AuthResponseModel> {
    return this.http.post<AuthResponseModel>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        this.setToken(response.token);

        const userData: CurrentUserDataModel = {
          email: response.email,
          role: response.role,
        };
        this.setUserData(userData);
      })
    );
  }

  register(data: AuthRequestModel): Observable<AuthResponseModel> {
    return this.http.post<AuthResponseModel>(`${this.apiUrl}/register`, data).pipe(
      tap((response) => {
        this.setToken(response.token);

        const userData: CurrentUserDataModel = {
          email: response.email,
          role: response.role,
        };
        this.setUserData(userData);
      })
    );
  }

  setToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  setUserData(data: CurrentUserDataModel) {
    localStorage.setItem(this.USER_DATA_KEY, JSON.stringify(data));
    this.loggedInUser.set(data);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken() && !!this.loggedInUser();
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_DATA_KEY);
    this.loggedInUser.set(null);
    this.router.navigate(['/login']);
  }

  askForAdministrator() {
    this.http.put(`${this.apiUrl}/updateRole`, { newRole: "ROLE_ORGANIZER" }, {
      headers: {
        Authorization: `Bearer ${this.getToken()}`,
      },
    }).subscribe({
      next: () => {
        console.log("Solicitud enviada correctamente");
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error("Error al solicitar ser administrador: ", error)
      }
    });
  }
}
