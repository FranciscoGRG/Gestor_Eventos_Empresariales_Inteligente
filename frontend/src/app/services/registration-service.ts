import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { RegistrationModel } from '../models/registration-model';

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  private apiUrl = 'http://localhost:8082/api/registrations';
  private readonly TOKEN_KEY = 'jwt_token';
  private http = inject(HttpClient);
  private router = inject(Router);

  createRegistration(eventId: number): Observable<void> {
    const token = localStorage.getItem(this.TOKEN_KEY);

    return this.http.post<void>(`${this.apiUrl}`, {
      eventId,
    }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  getAllRegistrationsByUserId(): Observable<RegistrationModel[]> {
    const token = localStorage.getItem(this.TOKEN_KEY);
    return this.http.get<RegistrationModel[]>(`${this.apiUrl}/user`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
