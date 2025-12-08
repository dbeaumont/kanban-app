import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface Task {
  id: string;
  boardId: string;
  columnId: string;
  title: string;
  description: string;
  status: string;
  assigneeId: string;
  labels: string;
}

@Injectable({ providedIn: 'root' })
export class TaskApiService {
  private base = `${environment.apiBaseUrl}/tasks`;
  constructor(private http: HttpClient) {}
  list(): Observable<Task[]> { return this.http.get<Task[]>(this.base); }
  move(id: string, columnId: string): Observable<Task> {
    return this.http.patch<Task>(`${this.base}/${id}/move`, null, { params: { columnId } });
  }
}
