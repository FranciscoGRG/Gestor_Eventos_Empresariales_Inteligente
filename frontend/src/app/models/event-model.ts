export interface EventModel {
    id: number;
    title: string;
    description: string;
    organizerId: number;
    location: string;
    startDate: Date;
    endDate: Date;
    capacity: number;
    numRegistered: number;
    isPublished: boolean;
    status: string;
}