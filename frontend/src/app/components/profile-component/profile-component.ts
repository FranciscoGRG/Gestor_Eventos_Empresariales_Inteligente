import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { EventService } from '../../services/event-service';
import { RegistrationService } from '../../services/registration-service';
import { EventModel } from '../../models/event-model';
import { RegistrationModel } from '../../models/registration-model';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventRequestModel } from '../../models/event-request-model';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-profile-component',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './profile-component.html',
})
export class ProfileComponent implements OnInit {
  actibeTab: 'events' | 'registrations' = 'events';
  events = signal<EventModel[]>([]);
  registrations = signal<RegistrationModel[]>([]);
  loading = signal(true);
  eventForm!: FormGroup;
  errorMessage: string | null = null;

  private eventService = inject(EventService);
  private authService = inject(AuthService)
  private registrationService = inject(RegistrationService);
  private fb = inject(FormBuilder);


  ngOnInit(): void {
    this.loadEvents();
    this.loadRegistrations();
    this.createEventForm();
  }

  createEventForm() {
    this.eventForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      capacity: ['', Validators.required],
      isPublished: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.eventForm.valid) {
      const event: EventRequestModel = this.eventForm.value;
      this.eventService.createEvent(event).subscribe({
        next: (event) => {
          this.events.set([...this.events(), event]);
          this.eventForm.reset();
        },
        error: (error) => {
          console.error("Error al crear el evento: ", error)
          this.errorMessage = "Error al crear el evento";
        }
      });
    }
  }

  loadEvents() {
    this.loading.set(true);
    this.eventService.getMineEvents().subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: (error) => {
        console.error("Error al cargar los eventos del usuario: ", error)
        this.events.set([]);
        this.loading.set(false);
      }
    });
  }

  loadRegistrations() {
    this.registrationService.getAllRegistrationsByUserId().subscribe({
      next: (registrations) => {
        this.registrations.set(registrations);
        this.loading.set(false);
      },
      error: (error) => {
        console.error("Error al cargar las inscripciones del usuario: ", error)
        this.registrations.set([]);
        this.loading.set(false);
      }
    });
  }

  onDeleteEvent(eventId: number): void {
    this.eventService.deleteEvent(eventId).subscribe({
      next: () => {
        this.loadEvents();
      },
      error: (error) => {
        console.error("Error al eliminar el evento: ", error)
        this.errorMessage = "Error al eliminar el evento";
      }
    });
  }
  
  onUpdateEventStatus(newStatus: string, eventId: number): void {
    this.eventService.updateEventStatus(newStatus, eventId).subscribe({
      next: () => {
        this.loadEvents();
      },
      error: (error) => {
        console.error("Error al actualizar el estado del evento: ", error)
        this.errorMessage = "Error al actualizar el estado del evento";
      }
    });
  }

  onAskForOrganizer(): void {
    this.authService.askForAdministrator();
  }

  isOrganizer(): boolean {
    return this.authService.isOrganizer();
  }
}
