import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { EventRequestModel } from '../models/event-request-model';
import { Observable } from 'rxjs';
import { EventModel } from '../models/event-model';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private apiUrl = 'http://localhost:8082/api/events';
  private readonly TOKEN_KEY = 'jwt_token';
  private http = inject(HttpClient);
  private router = inject(Router);

  createEvent(event: EventRequestModel): Observable<EventModel> {
    const token = localStorage.getItem(this.TOKEN_KEY);

    return this.http.post<EventModel>(`${this.apiUrl}`, event, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  updateEvent(eventId: number, event: EventRequestModel): Observable<EventModel> {
    const token = localStorage.getItem(this.TOKEN_KEY);

    return this.http.put<EventModel>(`${this.apiUrl}/${eventId}`, event, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  deleteEvent(eventId: number): Observable<void> {
    const token = localStorage.getItem(this.TOKEN_KEY);

    return this.http.delete<void>(`${this.apiUrl}/${eventId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  getPublishedEventById(eventId: number): Observable<EventModel> {
    return this.http.get<EventModel>(`${this.apiUrl}/${eventId}`)
  }

  getPublishedEventsByTitle(title: string): Observable<EventModel[]> {
    return this.http.get<EventModel[]>(`${this.apiUrl}/title/${title}`)
  }

  getEventsByOrganizerId(organizerId: number): Observable<EventModel[]> {
    return this.http.get<EventModel[]>(`${this.apiUrl}/organizer/${organizerId}`)
  }

  getAllPublishedAndActiveEvents(): Observable<EventModel[]> {
    return this.http.get<EventModel[]>(`${this.apiUrl}`)
  }

  getMineEvents(): Observable<EventModel[]> {
    const token = localStorage.getItem(this.TOKEN_KEY);

    return this.http.get<EventModel[]>(`${this.apiUrl}/mine`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  updateEventStatus(statusDisplay: string, eventId: number): Observable<EventModel> {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const apiStatus = statusDisplay === 'Publicado' ? 'ACTIVE' : 'DRAFT';

    return this.http.put<EventModel>(`${this.apiUrl}/${eventId}/status`, { newStatus: apiStatus }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
