import { IsEmail, IsString, MinLength } from 'class-validator';

export class SignInRequestDto {
  @IsEmail() email: string;
  @IsString() @MinLength(8) password: string;
}

export class SignInDataDto {
  userId: number;
  email: string;
  role: 'USER' | 'ADMIN';
}
