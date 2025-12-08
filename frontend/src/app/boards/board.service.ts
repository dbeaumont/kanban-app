import { Injectable, Pipe, PipeTransform } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface Board {
  id: string;
  name: string;
  description: string;
  ownerId: string;
}

@Injectable({ providedIn: 'root' })
export class BoardApiService {
  private base = `${environment.apiBaseUrl}/boards`;
  constructor(private http: HttpClient) {}
  list(): Observable<Board[]> { return this.http.get<Board[]>(this.base); }
  get(id: string): Observable<Board> { return this.http.get<Board>(`${this.base}/${id}`); }
}

@Pipe({ name: 'columnFilter', standalone: true })
export class ColumnFilterPipe implements PipeTransform {
  transform(tasks: any[], columnId: string) {
    return tasks?.filter(t => t.columnId === columnId) ?? [];
  }
}
