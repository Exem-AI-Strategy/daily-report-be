import { AuthService } from './auth.service';
import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { SignInDataDto, SignInRequestDto } from './dto/signin-user.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @HttpCode(HttpStatus.OK)
  @Post('signin')
  async signin(@Body() dto: SignInRequestDto) {
    const user = await this.authService.validateUser(dto.email, dto.password);
    const jwtToken = this.authService.generateJwt(user);

    const data: SignInDataDto = {
      userId: Number(user.user_id),
      email: user.email,
      role: user.role,
    };

    return { status: 'SUCCESS', data: data, token: jwtToken };
  }
}
